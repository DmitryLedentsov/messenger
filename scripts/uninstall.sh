#!/bin/bash

# Удаление всех зависимостей
echo "Удаление зависимостей..."

# Удаление Java
rm -rf jdk-21.0.2

# Удаление Maven
rm -rf maven-mvnd-1.0.2-linux-amd64

# Удаление PostgreSQL
apt-get remove --purge -y postgresql-17
rm -rf /etc/postgresql/
rm -rf /var/lib/postgresql/
rm /etc/apt/sources.list.d/pgdg.list

# Удаление других пакетов
apt-get remove --purge -y python3-doit python3-autopep8
apt-get autoremove -y

echo "Удаление завершено!"