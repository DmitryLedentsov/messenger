#!/bin/bash

# Создание таблиц в базе данных
echo "Создание таблиц..."

# Параметры подключения
DB_HOST="localhost"
DB_PORT=5435
DB_USER="postgres"
DB_NAME="postgres"  # Используем стандартную базу, можно изменить
PGPASSWORD="athyfylj"

# Проверяем наличие файла schema.sql
if [ ! -f "./schema.sql" ]; then
    echo "Ошибка: файл schema.sql не найден!"
    exit 1
fi

# Выполняем SQL скрипт
export PGPASSWORD
psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f ./schema.sql

echo "Таблицы созданы!"