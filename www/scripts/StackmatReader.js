function StackmatReader(updateCallback) {

	// compatibility fixes
	navigator.getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.msGetUserMedia;
	window.AudioContext = window.AudioContext || window.webkitAudioContext;

	var This = this;
	This.stream = null;
	This.audioContext = null;
	This.period = 0;
	This.updateCallback = updateCallback;

	// variables used for the processing
	This.idle = true;
	This.sampleOffset = 0;
	This.lastValue = 0;
	This.lastBit = true;
	This.inverted = false;
	This.bitArray = new Array();
	This.scriptNode = null;
	This.gen3 = false;

	This.error = function(error) {
		// Access to microphone denied
		alert("Zugriff auf Mikrofonspur fehlgeschlagen.");
		console.log(error);
	};

	This.mozWorkaround = function(stream) {
		// creates a random audio element and attaches the stream to it
		// This keeps the stream alive in firefox
		var a = document.createElement("audio");
		a.volume = 0;
		a.mozSrcObject = stream;
		a.play();
	};

	This.processBitArray = function(bitArray) {

		var asciis = new Array();
		for (var i = 0; i < bitArray.length; i += 10) {
			// START, LSB, ..., MSB, STOP
			var num = bitArray[i + 1] ? 1 : 0;
			num += bitArray[i + 2] ? 2 : 0;
			num += bitArray[i + 3] ? 4 : 0;
			num += bitArray[i + 4] ? 8 : 0;
			num += bitArray[i + 5] ? 16 : 0;
			num += bitArray[i + 6] ? 32 : 0;
			num += bitArray[i + 7] ? 64 : 0;
			num += bitArray[i + 8] ? 128 : 0;
			asciis.push(num);
		}

		var digits = new Array();
		digits[0] = parseInt(String.fromCharCode(asciis[1]));
		digits[1] = parseInt(String.fromCharCode(asciis[2]));
		digits[2] = parseInt(String.fromCharCode(asciis[3]));
		digits[3] = parseInt(String.fromCharCode(asciis[4]));
		digits[4] = parseInt(String.fromCharCode(asciis[5]));
		if (This.gen3)
			digits[5] = parseInt(String.fromCharCode(asciis[6]));
		else
			digits[5] = 0;

		// calculating the checksum (64 + sum of digits)
		var sum = 64 + digits[0] + digits[1] + digits[2] + digits[3] + digits[4] + digits[5];

		var offset = This.gen3 ? 1 : 0;

		// slice off the checksum-char and the \n\r
		var string = String.fromCharCode.apply(null, asciis.slice(0, offset + 6));

		// Check if 1st character is valid, checksum ok and last 2 chars \n and \t
		var ok = " ACILRS".indexOf(asciis[0] > -1) && sum == asciis[offset + 6] && asciis[offset + 7] == 10 && asciis[offset + 8] == 13;

		if (ok) {

			// Now turn the digits into milliseconds
			var ms = 0;
			ms += digits[0];
			ms *= 6;
			ms += digits[1];
			ms *= 10;
			ms += digits[2];
			ms *= 10;
			ms += digits[3];
			ms *= 10;
			ms += digits[4];
			ms *= 10;
			ms += digits[5];

			This.updateCallback(ms, string[0]);

		} else {
			// console.log("invalid package");
		}
	};

	This.process = function(audio) {

		// Merge the channels.
		var input = audio.inputBuffer;
		var data = input.getChannelData(0);

		// Start, where last block ended to stay in the period
		var i = This.sampleOffset;

		var packetLength = This.gen3 ? 100 : 90;

		while (i < data.length) {

			// read from buffer
			var value = data[Math.floor(i)];

			// If the signal is inverted
			if (This.inverted)
				value *= -1;

			// The firefox somehow smudges the signal: The values are not always
			// properly over or under 0
			// => You cannot distinguish the bytes by a threshold.
			// Therefore the bits get distinguished by the difference to the
			// previous period sample
			// This is a little unstable, but I would say >90% of the time
			// successful.
			var bit;
			// If the signal fell by > 0.4 AND is now < 0.2
			if (This.lastValue - value > 0.4 && value < 0.2)
				bit = 1;
			// If the signal rose by > 0.4 AND is now > -0.2
			else if (value - This.lastValue > 0.4 && value > -0.2)
				bit = 0;
			// otherwise the bit did not change
			else
				bit = This.lastBit;

			// The idle-state consists of 1's
			// => first 0 gets the script out of idle, trying to fetch data
			if (!bit)
				This.idle = false;

			if (!This.idle) {

				// Add current bit to stack and read new length
				var length = This.bitArray.push(bit);

				// mark as corrupted if there is a missing start or stop bit
				var corrupted = ((length % 10 == 1 && bit) || (length % 10 == 0 && !bit));

				if (corrupted) {
					// skip 1 period ahead
					i += This.period;
					// try inverting the signal.
					This.inverted = !This.inverted;
					console.log("corrupted: missing start or stop bit at array length " + This.bitArray.length);
				}

				// 90 (or gen3: 100) databits make 1 packet. Process it.
				if (length >= packetLength) {
					This.processBitArray(This.bitArray);
				}

				// if packet is over or data is corrupted, switch back to idle
				if (length >= packetLength || corrupted) {
					This.bitArray = new Array();
					This.idle = true;
				}
			}

			// update values
			This.lastValue = value;
			This.lastBit = bit;

			// If the script is fetching data, always stay in the period.
			// If the script is in idle state, scan in some smaller, odd periods
			// Goal: The period offset changes to prevent the scan points to be
			// constantly at the flank changes.
			i += This.idle ? This.period / 1.141 : This.period;
		}

		// It somehow does not work in chrome if the scriptNode is not connected
		// to the output. => Output it mute
		var outData = audio.outputBuffer.getChannelData(0);
		for (var sample = 0; sample < outData.length; sample++) {
			outData[sample] = data[sample] * 0; // mute
		}

		// Stay in the period next script call
		This.sampleOffset = i - data.length;

	};

	This.gotStream = function(stream) {

		console.log("Got stream!");

		// Prevents the stream from dying in firefox
		if (/Firefox/i.test(navigator.userAgent))
			This.mozWorkaround(stream);

		This.stream = stream;
		This.audioContext = new AudioContext();
		This.period = This.audioContext.sampleRate / 1200;

		// buffer -> script (-> output if debug)
		var bufferSrc = This.audioContext.createMediaStreamSource(stream);
		This.scriptNode = This.audioContext.createScriptProcessor(4096, 1, 1);
		This.scriptNode.onaudioprocess = This.process;

		bufferSrc.connect(This.scriptNode);
		This.scriptNode.connect(This.audioContext.destination);

		// Note: The reference to scriptNode is global to fix a chrome bug.
		// The garbage collection would otherwise kill the object and stop all
		// onaudioprocess calls
	};

	This.startRecording = function(callback) {

		// Request microphone access
		navigator.getUserMedia({
			audio : true
		}, function(stream) {
			// access granted
			callback();
			This.gotStream(stream);
		}, This.error);

	};

	This.stopRecording = function() {

		// Just stop the media stream
		This.stream.stop();
		This.scriptNode.onaudioprocess = null;

	};

}