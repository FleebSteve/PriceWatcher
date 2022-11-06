sudo setenforce 0
sudo systemctl start docker.service
sudo chmod 777 /var/run/docker.sock
docker-compose run --service-ports mongodb

