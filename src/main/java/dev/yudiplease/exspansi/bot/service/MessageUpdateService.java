package dev.yudiplease.exspansi.bot.service;

//@Service
//public class MessageUpdateService extends MessageListener implements EventListener<MessageUpdateEvent> {
//
//
//    @Override
//    public Class<MessageUpdateEvent> getEventType() {
//        return MessageUpdateEvent.class;
//    }
//
//    @Override
//    public Mono<Void> execute(MessageUpdateEvent event) {
//        return Mono.just(event)
//                .filter(MessageUpdateEvent::isContentChanged)
//                .flatMap(MessageUpdateEvent::getMessage)
//                .flatMap(super::processMessage);
//    }
//}
