package io.eventuate.examples.tram.sagas.ordersandcustomers.orders.service;

import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.commandsandreplies.RejectOrderCommand;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.Order;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.OrderRepository;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.commandsandreplies.ApproveOrderCommand;
import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

import java.util.Optional;

public class OrderCommandHandler {

  @Autowired
  private OrderRepository orderRepository;

  public CommandHandlers commandHandlerDefinitions() {
    return SagaCommandHandlersBuilder
            .fromChannel("orderService")
            .onMessage(ApproveOrderCommand.class, this::approve)
            .onMessage(RejectOrderCommand.class, this::reject)
            .build();
  }

  public Message approve(CommandMessage<ApproveOrderCommand> cm) {
    long orderId = cm.getCommand().getOrderId();
    Optional<Order> order = orderRepository.findById(orderId);
    order.get().noteCreditReserved();
    return withSuccess();
  }

  public Message reject(CommandMessage<RejectOrderCommand> cm) {
    long orderId = cm.getCommand().getOrderId();
    Optional<Order> order = orderRepository.findById(orderId);
    order.get().noteCreditReservationFailed();
    return withSuccess();
  }

}
