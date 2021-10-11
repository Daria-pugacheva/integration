package ru.gb.pugacheva.integration;

import com.rabbitmq.client.*;

import java.util.Scanner;

public class CaprisiousReader {

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare("articles_exchanger", BuiltinExchangeType.DIRECT);
        String queueName = channel.queueDeclare().getQueue();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Выберите темы статей для просмотра путем ввода команды: \n" +
                "Подписаться на рассылку: set_topic интересующая_тема (варианты тем: php, c++, java, python).\n" +
                "Отписаться от рассылки: del_topic интересующая_тема");

        while (scanner.hasNext()) {
            String[] messageDetails = scanner.nextLine().split(" ");

            if (messageDetails[0].equals("set_topic")) {
                channel.queueBind(queueName, "articles_exchanger", messageDetails[1]);
                System.out.println(" [*] Waiting for messages with routing key (" + messageDetails[1] + "):");
            } else if (messageDetails[0].equals("del_topic")) {
                channel.queueUnbind(queueName, "articles_exchanger", messageDetails[1]);
                System.out.println(" [*] Stop waiting for messages with routing key (" + messageDetails[1] + "):");
            }

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String article = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + article + "'");

            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });
        }
    }
}
