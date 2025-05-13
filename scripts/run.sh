sudo systemctl stop messenger.service
yes | cp /home/messenger/scripts/messenger.service /usr/lib/systemd/system
systemctl daemon-reload
sudo systemctl start messenger.service