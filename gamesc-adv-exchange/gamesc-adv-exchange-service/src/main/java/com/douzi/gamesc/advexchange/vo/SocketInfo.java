package com.douzi.gamesc.advexchange.vo;

import java.net.Socket;
import lombok.Data;

@Data
public class SocketInfo {

    /**
     * socket
     */
    private Socket socket;
    /**
     * 是否空闲 （是：true  否：false）
     */
    private boolean isFree;
    /**
     * socket id
     */
    private Integer socketId;
    /**
     * 是否为可关闭链接 （是：true  否：false）
     */
    private boolean isClosed;
}
