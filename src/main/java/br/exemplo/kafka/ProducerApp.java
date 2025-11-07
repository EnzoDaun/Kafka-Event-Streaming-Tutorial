package br.exemplo.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.Properties;

/**
 * Producer simples que envia 20 mensagens para o tópico "eventos".
 * Força mensagens 1-10 para partição 0 e mensagens 11-20 para partição 1
 * para demonstrar distribuição manual entre partições.
 */
public class ProducerApp {
    public static void main(String[] args) throws InterruptedException {
        String bootstrap = "localhost:9092";
        Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap); // Define onde o producer deve se conectar para enviar mensagens
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); // Converte strings em bytes para enviar as chaves das mensagens
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());// Converte strings em bytes para enviar os valores das mensagens

        // "all" = aguarda confirmação de todos os brokers (máxima durabilidade)
        // "1" = aguarda confirmação apenas do líder
        // "0" = não aguarda confirmação (máxima velocidade, menor durabilidade)
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        try (Producer<String, String> producer = new KafkaProducer<>(props)) {

            String topic = "eventos"; // Nome do tópico onde as mensagens serão enviadas

            // LOOP: Envia 20 mensagens numeradas de 1 a 20
            for (int i = 1; i <= 20; i++) {

                String value = "mensagem-" + i; // CRIAÇÃO DA MENSAGEM: Valor da mensagem com número sequencial
                int partition = (i <= 10) ? 0 : 1; // LÓGICA DE PARTICIONAMENTO: Distribui mensagens entre partições

                // CRIAÇÃO DO RECORD: Objeto que representa uma mensagem
                ProducerRecord<String, String> record = new ProducerRecord<>(topic, partition, null, value);

                // ENVIO ASSÍNCRONO: Envia a mensagem e define callback
                producer.send(record, (metadata, exception) -> {
                    if (exception != null) {
                        exception.printStackTrace();
                    } else {
                        System.out.printf("Enviado -> value=%s partition=%d offset=%d%n", // Se o envio foi bem-sucedido, exibe informações da mensagem
                                value, // Valor da mensagem enviada
                                metadata.partition(), // Partição onde foi armazenada
                                metadata.offset()); // Offset atribuído na partição
                    }
                });
                Thread.sleep(200);
            }
            // Força o envio de todas as mensagens pendentes e Garante que todas as mensagens foram enviadas
            producer.flush();
        }
    }
}
