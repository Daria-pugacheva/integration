package ru.gb.pugacheva.integration;

import com.rabbitmq.client.*;

import java.util.Scanner;

public class Reader {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Выберите тему статей для просмотра путем ввода команды: " +
                "set_topic интересующая_тема (варианты тем: php, c++, java, python)");

        String[] messageDetails = scanner.nextLine().split(" ");
        String routingKey = messageDetails[1];

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare("articles_exchanger", BuiltinExchangeType.DIRECT);

        String queueName = channel.queueDeclare().getQueue();

        channel.queueBind(queueName, "articles_exchanger", routingKey);
        System.out.println(" [*] Waiting for messages with routing key (" + routingKey + "):");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String article = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + article + "'");

        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }

}

