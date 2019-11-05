set -e
read -e -p "Location to OCS start script: " -i "/home/ocs/server/start.sh" startscript
servicefile="/etc/systemd/system/ocs.service"
echo "service will be installed at $servicefile"
chmod u+x $startscript
sudo cp ocs.service $servicefile
sudo sed -i -e "s!STARTSCRIPT!$startscript!g" $servicefile
sudo chmod 644 $servicefile
echo "Copied service file to $servicefile, given 755 permission"
sudo systemctl enable ocs
sudo systemctl start ocs
echo "Installed and started service $service_name"
(sudo crontab -l ; echo "4 0 * * * systemctl restart ocs") | sudo crontab -
echo "Added root crontab for daily restarts"
echo "Success!"
