package br.exemplo.kafka;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * Consumer simples que faz parte do group "grupo-demo".
 * Para demonstrar consumer groups, rode duas instâncias desta classe:
 * cada instância receberá partições diferentes (com 2 partições).
 */
public class ConsumerApp {
    public static void main(String[] args) {
        String bootstrap = "localhost:9092";
        Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap); // Define onde o consumer deve se conectar
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "grupo-demo"); // Consumers com mesmo group.id dividem as partições entre si
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()); // Converte bytes recebidos em strings para as chaves das mensagens
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()); // Converte bytes recebidos em strings para os valores das mensagens
        // Comportamento quando não há offset commitado
        // "earliest" = ler desde o início do tópico
        // "latest" = ler apenas mensagens novas
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true"); // true = Kafka automaticamente marca mensagens como processadas

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {

            consumer.subscribe(Collections.singletonList("eventos")); // Consumer se inscreve no tópico "eventos" - Collections.singletonList() cria uma lista com um único elemento
            System.out.printf("Consumer iniciado (thread=%s). Aguardando mensagens...%n", Thread.currentThread().getName());
            while (true) {  // Consumer fica sempre escutando por novas mensagens
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000)); // Busca mensagens por até 1 segundo (1000ms) Se não houver mensagens, retorna uma coleção vazia

                for (ConsumerRecord<String, String> rec : records) { // Itera sobre todas as mensagens recebidas
                    System.out.printf("Consumer[%s] recebeu -> partition=%d offset=%d key=%s value=%s%n", // Mostra informações detalhadas de cada mensagem
                            Thread.currentThread().getName(),  // Nome da thread atual
                            rec.partition(), // Número da partição (0 ou 1)
                            rec.offset(), // Offset da mensagem na partição
                            rec.key(), // Chave da mensagem (pode ser null)
                            rec.value()); // Valor/conteúdo da mensagem
                }
            }
        }
    }
}
