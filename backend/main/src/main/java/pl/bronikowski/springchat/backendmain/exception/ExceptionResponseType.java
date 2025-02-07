package pl.bronikowski.springchat.backendmain.exception;

public enum ExceptionResponseType {
    NOT_FOUND,
    CHANNEL_OPERATION_NOT_ENOUGH_RIGHTS,
    CHANNEL_MEMBER_ALREADY_ADDED,
    STOMP_DESTINATION_FORBIDDEN,
    VIDEO_ROOM_ALREADY_JOINED,
    VIDEO_ROOM_PARTICIPANT_NOT_PRESENT
}
