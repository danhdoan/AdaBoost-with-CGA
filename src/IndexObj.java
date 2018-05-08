/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author admin
 */
public class IndexObj {
    private byte type;
    private byte x;
    private byte y;
    private byte h;
    private byte w;
    
    IndexObj(byte type, byte x, byte y, byte h, byte w) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.h = h;
        this.w = w;
    }

    public byte getType() {
    	return this.type;
    }

    public byte getX() {
    	return this.x;
    }

    public byte getY() {
    	return this.y;
    }

    public byte getH() {
    	return this.h;
    }

    public byte getW() {
    	return this.w;
    }
}
