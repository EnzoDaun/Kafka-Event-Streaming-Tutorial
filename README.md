# Event Streaming com Apache Kafka + Java

Projeto de demonstração para trabalho da disciplina de Sistemas Distribuidos:
**Event Streaming com Apache Kafka – partições e consumer groups**

Conteúdo:
- docker-compose.yml
- Código Java (Maven) com Producer e Consumer
- Instruções para criar o tópico, rodar 2 consumers no mesmo grupo e demonstrar reprocessamento

## Requisitos
- Docker & Docker Compose
- Java 17+
- Maven
- IntelliJ IDEA (opcional, mas recomendado)

## 1) Subir Kafka (Docker Compose)
No diretório do projeto execute:

```bash
docker compose pull
docker compose up -d
docker compose ps
docker logs -f kafka
```

Aguarde até o broker inicializar. Se quiser verificar onde estão os binários do Kafka dentro do container:
```bash
docker exec -it kafka /bin/sh -c "find / -name kafka-topics.sh 2>/dev/null || true"
```

## 2) Criar o tópico `eventos` com 2 partições
```bash
docker exec -it kafka /bin/sh -c "/opt/kafka/bin/kafka-topics.sh \
  --create --topic eventos --partitions 2 --replication-factor 1 \
  --bootstrap-server localhost:9092"
```
Verifique:
```bash
docker exec -it kafka /bin/sh -c "/opt/kafka/bin/kafka-topics.sh --describe --topic eventos --bootstrap-server localhost:9092"
```

## 3) Importar o projeto no IntelliJ
- File -> New -> Project from Existing Sources -> escolha o pom.xml
- Aguarde o Maven baixar dependências

## 4) Rodar a demo
1. Crie duas Run Configurations idênticas apontando para `br.exemplo.kafka.ConsumerApp` e execute **duas instâncias** (Ativar opção de run: Allow parallel run). Ambas usarão o mesmo `group.id = grupo-demo` e dividirão as partições.
2. Execute `br.exemplo.kafka.ProducerApp`. Observe no console as linhas `partition` e `offset` retornadas pelo callback do producer.

## 5) Comandos úteis
- Listar tópicos:
  `docker exec -it kafka /opt/kafka/bin/kafka-topics.sh --list --bootstrap-server localhost:9092`
- Descrever tópico:
  `docker exec -it kafka /opt/kafka/bin/kafka-topics.sh --describe --topic eventos --bootstrap-server localhost:9092`
- Ver offsets por grupo:
  `docker exec -it kafka /opt/kafka/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group grupo-demo`
- Parar e remover containers:
  `docker compose down -v`

## 6) Diagrama rápido
```
Producer -> Tópico:eventos
           /          \
      Partition 0   Partition 1
       |               |
   Consumer A       Consumer B (mesmo group: grupo-demo)
```
