package org.build.buybook.model;

/**
 * Created by MAC on 15/10/20.
 */
public class Book {
    public String name;
    public String publish;
    public String price;
    public String course;

    public Book(String name, String publish, String price, String course) {
        this.name = name;
        this.publish = publish;
        this.price = price;
        this.course = course;
    }
}
