# Rodando no container

A maneira mais fácil de levantar todo o sistema é usando Docker.

	$ docker-compose up

Isso vai construir e executar todos os containers.

Como pode ver, são definidas 3 imagens

Proxy - define um volume com as configurações do nginx a serem aplicadas e o conteúdo estático a ser servido

Backend - define o serviço que recebe os votos da interface WEB e consultas da produção

DB - define o banco de dados usado pelo backend

Este é apenas um exemplo de deployment. Para construir apenas o container backend

	$ docker build -t poll-service .

Rodando apenas o container backend

	$ docker run --rm -p 8080:8080 poll-service

# Rodando sem container

Para rodar a aplicação sem Docker você precisa ter *Leiningen* instalado.

Instalando Leiningen

1. Faça download do script em https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein
2. Coloque no $PATH onde o shell possa encontrá-lo (~/bin, por exemplo)
3. Configure para que possa ser executado com chmod a+x ~/bin/lein
4. Execute o script (digite lein) para iniciar a instalação

Instalado a ferramenta de build é hora de construir a aplicação

	$ lein build

Para rodar

	$ export HTTP_PORT=8080 && lein run

E acesse http://localhost:8080/

Para rodar os testes

	$ lein test

Para rodar os testes de integração

	$ lein test :integration

# API REST

### Votar
POST /poll HTTP/1.1

{
  "candidate":"O Candidato"
  "token":"abc...xyz"
}

### Admin
GET /votes HTTP/1.1

# Considerações gerais

+ A arquitetura da aplicação é organizada de forma que recursos considerados “stateful” possuem dependências e ciclo de vida gerenciados pelo framework de componentes. Este framework pode ser visto como um estilo de injeção de dependência usando estruturas de dados imutáveis.
+ O modelo de dados atualmente utilizado registra o candidato que recebeu o voto, além da hora, mas isso é flexível e pode ser modificado pra registrar outras informações (como IP do usuário, por exemplo).

# TODO

+ Testes funcionais para testar a aplicação do ponto de vista do usuário
+ Pensei em orquestrar nginx e múltiplos containers de backend usando Kubernetes, mas talvez seja exagero?
