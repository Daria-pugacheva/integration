package ru.gb.pugacheva.integration;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Publisher {
    //Реализован вариант, когда IT-блог не может быть в курсе, кто на него подпишется, поэтому единую очередь не создаю
    //А свои временные очереди создает сам пользователь
    //Соответственно, при таком раскладе выходит, что пользователь получит рассылку только в том случае, если он сначала
    //подписался на темы и запустился, а IT-блок запустился следущим. Иначе, если сначала запускается IT-блог, то
    //опубликованные им статьи улетают в пустоту. Ну и тогда мы тут рассматриваем статьи, как что-то, что
    //не хранится после публикации: т.е. если пользователь не был подключен к очереди в момент публикации, то он
    //промограл свое счастье.

    //Насколько понимаю, если создать некую именованную единую  очередь для обменника и пользователя, то Produser
    //класть в нее статьи независимо от того, подключен ли в настоящий момент пользователь или нет. А пользователь уже,
    //когда подключится, то заберет то, что ему по теме подходит.

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare("articles_exchanger", BuiltinExchangeType.DIRECT);

            channel.basicPublish("articles_exchanger", "php", null, "php article".getBytes("UTF-8"));
            System.out.println("Опубликована статья про php");
            channel.basicPublish("articles_exchanger", "c++", null, "c++ article".getBytes("UTF-8"));
            System.out.println("Опубликована статья про c++");
            channel.basicPublish("articles_exchanger", "java", null, "java article".getBytes("UTF-8"));
            System.out.println("Опубликована статья про java");
            channel.basicPublish("articles_exchanger", "python", null, "python article".getBytes("UTF-8"));
            System.out.println("Опубликована статья про python");
        }
    }
}

