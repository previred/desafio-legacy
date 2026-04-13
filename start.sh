#!/bin/bash

docker-compose down
echo "========================================"
echo "  Desafio Tecnico - Gestion Empleados   "
echo "  Daniel Ivan Ramos Truitrui            "
echo "  Empresa "
echo "========================================"

# Verificar que Docker esta instalado
if ! command -v docker &> /dev/null; then
    echo "ERROR: Docker no esta instalado."
    echo "Instala Docker desde https://www.docker.com/get-started"
    exit 1
fi

# Verificar que Docker Compose esta instalado
if ! command -v docker-compose &> /dev/null; then
    echo "ERROR: Docker Compose no esta instalado."
    exit 1
fi

# Verificar que Docker esta corriendo
if ! docker info &> /dev/null; then
    echo "ERROR: Docker no esta corriendo. Inicia Docker Desktop e intenta de nuevo."
    exit 1
fi

echo ""
echo "Construyendo y levantando la aplicacion..."
echo ""

docker-compose up --build -d

echo ""
echo "Esperando que la aplicacion levante..."
sleep 10

echo ""
echo "========================================"
echo "  Aplicacion lista!"
echo "  App:      http://localhost:8040"
echo "  H2:       http://localhost:8040/h2-console"
echo "========================================"
echo ""
echo "Para ver los logs:   docker-compose logs -f"
echo "Para detener:        docker-compose down"
echo ""