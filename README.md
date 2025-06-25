# Share Trip

Uma aplicação de viagens completa, concebida para ajudar viajantes de todo o mundo a descobrir, partilhar e conectar-se através das suas experiências de viagem. A app combina funcionalidades sociais com assistência baseada em IA para criar um verdadeiro companheiro de viagem.

## 🚀 Funcionalidades

* **Rede Social de Viagens**: Partilha publicações
* **Pontos de Interesse (POI)**: Descobre informações baseadas na localização
* **ChatBot com IA**: Obtém assistência de viagem com o modelo Llama3.2:1b
* **Serviços de Localização**: Integração com GPS para funcionalidades baseadas na localização

## 📁 Estrutura do Projeto

```
├── API/                    # Servidor backend em Node.js
│   ├── app.js             # Ficheiro principal do servidor
│   ├── datasetLoader.js   # Script de população da base de dados
│   ├── api/               # Rotas e controladores da API
│   ├── config/            # Ficheiros de configuração
│   ├── models/            # Modelos de dados
│   └── services/          # Serviços com lógica de negócio
├── APP/                   # Aplicação Android
│   └── app/src/main/java/com/example/sharetrip/
├── DB/                    # Scripts da base de dados e datasets
│   ├── share-trip_v2.sql           # Estrutura principal da base de dados
│   ├── share-trip_v2-INSERT.sql    # Dados iniciais
│   └── pointsOfInterestDataSet.csv # Dataset dos Pontos de Interesse
└── IMG/                   # Imagens e recursos do projeto
```

## 🛠️ Pré-requisitos

* **Node.js**
* Servidor de base de dados **MySQL** (recomenda-se [XAMPP](https://www.apachefriends.org/) para desenvolvimento)
* **Android Studio**
* **Ollama**

## ⚙️ Instruções de Configuração

### 1. Configurar a Base de Dados

1. Instala e inicia um servidor MySQL (recomenda-se [XAMPP](https://www.apachefriends.org/))
2. Cria a base de dados executando o script [`share-trip_v2.sql`](DB/share-trip_v2.sql)
3. Insere os dados iniciais com o script [`share-trip_v2-INSERT.sql`](DB/share-trip_v2-INSERT.sql)

### 2. Configurar o Servidor da API

1. Acede ao diretório da API:

   ```bash
   cd API
   ```

2. Instala as dependências:

   ```bash
   npm install
   ```

3. Configura as variáveis de ambiente:

   * Copia o ficheiro [`.env.template`](API/config/.env.template) para `.env` dentro do diretório `API/config/`
   * Atualiza os valores no ficheiro `.env`:

   ```bash
   DEV_MODE=TRUE
   DB_HOST=localhost
   DB_PORT=3306
   DB_USERNAME=root
   DB_PASSWORD=a_sua_password_mysql
   DB=sharetrip
   BCRYPT_SALT=10
   API_PORT=8800
   SECRET_KEY=chave_secreta_aqui
   ```

4. Carrega o dataset de POIs:

   ```bash
   node datasetLoader.js
   ```

   Aguarda até o processo terminar (isto vai popular a base de dados com os dados dos Pontos de Interesse a partir de [`pointsOfInterestDataSet.csv`](DB/pointsOfInterestDataSet.csv)).

### 3. Configurar o ChatBot com IA

1. Instala o [Ollama](https://ollama.com/download/)
2. Faz o download e corre o modelo de IA:

   ```bash
   ollama run llama3.2:1b
   ```

   Isto fará o download do modelo se ainda não estiver instalado.

### 4. Iniciar o Servidor da API

Inicia o servidor com um dos seguintes comandos:

```bash
# Execução normal
node app.js

# Modo de desenvolvimento (caso tenhas o nodemon instalado)
nodemon app.js
```

A API estará disponível em `http://localhost:8800` (ou na porta que tiveres configurado).

### 5. Configurar a App Android

1. Abre a pasta `APP` no Android Studio
2. Atualiza o endereço IP do servidor no ficheiro [`ApiClient.java`](APP/app/src/main/java/com/example/sharetrip/api/ApiClient.java)
3. Compila e executa a aplicação no teu dispositivo ou emulador

## 📱 Endpoints da API

A API fornece os seguintes grupos principais de endpoints:

* **🤖 ChatBot**: Chat com inteligência artificial
* **🗺️ Mapa**: Localizações e pontos de interesse
* **👥 Social**: Publicações
* **👤 Utilizador**: Autenticação e gestão de utilizadores

Para documentação detalhada da API, vê [`API/api/routes.md`](API/api/routes.md).

## 🧪 Testes

Atualmente, não existem testes automatizados configurados. Para testar:

1. Garante que todos os serviços estão a funcionar (Base de Dados, API, Ollama)
2. Usa a app Android para testar funcionalidades
3. Usa ferramentas como o Postman para testar endpoints da API

## 🤝 Contribuição

Este projeto foi desenvolvido por André Silva e Samuel Santos como parte de um projeto académico.

## 🆘 Resolução de Problemas

* **Erros na ligação à base de dados**: Verifica se o MySQL está a correr e se as credenciais no ficheiro `.env` estão corretas
* **API sem resposta**: Confirma se o servidor arrancou com sucesso e se a porta está disponível (por defeito: 8800)
* **ChatBot sem funcionar**: Verifica se o Ollama está instalado e se o modelo foi corretamente descarregado
* **Problemas de ligação da app Android**: Confirma o IP do servidor no ficheiro [`ApiClient.java`](APP/app/src/main/java/com/example/sharetrip/api/ApiClient.java)
* **Erro ao carregar os POIs**: Garante que a base de dados está corretamente configurada antes de correres o [`datasetLoader.js`](API/datasetLoader.js)

### Configuração do Ambiente

Certifica-te de que o teu ficheiro [`.env`](API/config/.env) está corretamente configurado com:

* Credenciais corretas da base de dados
* Chave secreta válida para autenticação JWT
* Definições de porta apropriadas

---

# Share Trip

A comprehensive travel application designed to help travelers worldwide discover, share, and connect through their travel experiences. The app combines social features with AI-powered assistance to create a complete travel companion.

## 🚀 Features

- **Social Travel Network**: Share posts
- **Points of Interest (POI)**: Discover location-based information
- **AI ChatBot**: Get travel assistance powered by Llama3.2:1b model
- **Location Services**: GPS integration for location-based features

## 📁 Project Structure

```
├── API/                    # Node.js backend server
│   ├── app.js             # Main server file
│   ├── datasetLoader.js   # Database population script
│   ├── api/               # API routes and controllers
│   ├── config/            # Configuration files
│   ├── models/            # Data models
│   └── services/          # Business logic services
├── APP/                   # Android application
│   └── app/src/main/java/com/example/sharetrip/
├── DB/                    # Database scripts and datasets
│   ├── share-trip_v2.sql           # Main database schema
│   ├── share-trip_v2-INSERT.sql    # Initial data
│   └── pointsOfInterestDataSet.csv # POI dataset
└── IMG/                   # Project images and assets
```

## 🛠️ Prerequisites

- **Node.js**
- **MySQL** database server (XAMPP recommended for development)
- **Android Studio**
- **Ollama**

## ⚙️ Setup Instructions

### 1. Database Setup

1. Install and start a MySQL server (we recommend [XAMPP](https://www.apachefriends.org/))
2. Create the database by running the [`share-trip_v2.sql`](DB/share-trip_v2.sql) script
3. Populate with initial data using [`share-trip_v2-INSERT.sql`](DB/share-trip_v2-INSERT.sql)

### 2. API Server Setup

1. Navigate to the API directory:
   ```bash
   cd API
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Configure environment variables:
   - Copy [`.env.template`](API/config/.env.template) to `.env` in the `API/config/` directory
   - Update the configuration values in `.env`:
   ```bash
   DEV_MODE=TRUE
   DB_HOST=localhost
   DB_PORT=3306
   DB_USERNAME=root
   DB_PASSWORD=your_mysql_password
   DB=sharetrip
   BCRYPT_SALT=10
   API_PORT=8800
   SECRET_KEY=your_secret_key_here
   ```

4. Load the POI dataset:
   ```bash
   node datasetLoader.js
   ```
   Wait for the process to complete (this will populate the database with Points of Interest data from [`pointsOfInterestDataSet.csv`](DB/pointsOfInterestDataSet.csv)).

### 3. AI ChatBot Setup

1. Install [Ollama](https://ollama.com/download/)
2. Download and run the AI model:
   ```bash
   ollama run llama3.2:1b
   ```
   This will download the model if it's not already installed.

### 4. Launch the API Server

Start the server using one of the following commands:

```bash
# Standard launch
node app.js

# Development mode (if you have nodemon installed)
nodemon app.js
```

The API will be available at `http://localhost:8800` (or your configured port).

### 5. Android App Setup

1. Open the `APP` folder in Android Studio
2. Update the server IP address in [`ApiClient.java`](APP/app/src/main/java/com/example/sharetrip/api/ApiClient.java)
3. Build and run the application on your device or emulator

## 📱 API Endpoints

The API provides the following main endpoint categories:

- **🤖 ChatBot**: AI-powered chat
- **🗺️ Map**: Locations, points of interest  
- **👥 Social**: Posts
- **👤 User**: Authentication and user management

For detailed API documentation, see [`API/api/routes.md`](API/api/routes.md).

## 🧪 Testing

Currently, no automated tests are configured. To test:

1. Ensure all services are running (Database, API, Ollama)
2. Use the Android app to test functionality
3. Use API testing tools like Postman for endpoint testing

## 🤝 Contributing

This project was developed by André Silva and Samuel Santos as part of an academic project.

## 🆘 Troubleshooting

- **Database connection errors**: Ensure MySQL is running and credentials are correct in your `.env` file
- **API not responding**: Check if the server started successfully and ports are available (default: 8800)
- **ChatBot not working**: Verify Ollama is installed and the model is downloaded
- **Android app connection issues**: Confirm the API server IP address in [`ApiClient.java`](APP/app/src/main/java/com/example/sharetrip/api/ApiClient.java)
- **POI dataset loading fails**: Ensure the database is properly set up before running [`datasetLoader.js`](API/datasetLoader.js)

### Environment Configuration

Make sure your [`.env`](API/config/.env) file is properly configured with:
- Correct database credentials
- Valid secret key for JWT authentication
- Appropriate port settings
