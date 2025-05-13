#!/bin/bash

# Настройка базы данных
echo "Настройка PostgreSQL..."

# Пароль для PostgreSQL
PGPASSWORD="athyfylj"
DB_PORT=5435
CLUSTER_NAME="messenger"

# Проверяем, существует ли кластер
if ! pg_lsclusters | grep -q "$CLUSTER_NAME"; then
    echo "Создание кластера PostgreSQL..."
    echo "$PGPASSWORD" > pwfile.conf
    pg_createcluster -p $DB_PORT 17 $CLUSTER_NAME -- --auth=scram-sha-256 --username=postgres --pwfile=$(pwd)/pwfile.conf
    rm pwfile.conf
fi

# Запускаем кластер, если не запущен
if ! pg_ctlcluster 17 $CLUSTER_NAME status | grep -q "online"; then
    echo "Запуск кластера PostgreSQL..."
    pg_ctlcluster 17 $CLUSTER_NAME start
fi

echo "Настройка завершена!"