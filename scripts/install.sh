#!/bin/bash

# Установка всех зависимостей
echo "Установка зависимостей..."

# Обновление пакетов
apt-get update -y && apt-get upgrade -y

# Установка Java 21
if ! command -v java &> /dev/null; then
    echo "Установка Java..."
    wget https://download.java.net/java/GA/jdk21.0.2/f2283984656d49d69e91c558476027ac/13/GPL/openjdk-21.0.2_linux-x64_bin.tar.gz
    tar -xvzf openjdk-21.0.2_linux-x64_bin.tar.gz
    rm openjdk-21.0.2_linux-x64_bin.tar.gz
    echo "export PATH=\$PATH:$(pwd)/jdk-21.0.2/bin/" >> ~/.bashrc
fi



# Установка PostgreSQL
if ! command -v psql &> /dev/null; then
    echo "Установка PostgreSQL..."
    apt-get install -y lsb-release curl ca-certificates
    install -d /usr/share/postgresql-common/pgdg
    curl -o /usr/share/postgresql-common/pgdg/apt.postgresql.org.asc --fail https://www.postgresql.org/media/keys/ACCC4CF8.asc
    echo "deb [signed-by=/usr/share/postgresql-common/pgdg/apt.postgresql.org.asc] https://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list
    apt-get update -y
    apt-get install -y postgresql-17
fi


echo "Установка завершена!"
