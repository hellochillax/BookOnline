package org.build.buybook.model;

import java.util.List;

/**
 * Created by MAC on 15/10/20.
 */
public class Book {
    public String book_id;
    public String book_name;
    public String author;
    public String pub_house;
    public String course_name;
    public String version;
    public String book_price;
    public String ordered;

    public Book(String book_id, String book_name, String author, String pub_house, String course_name, String version, String book_price) {
        this.book_id = book_id;
        this.book_name = book_name;
        this.author = author;
        this.pub_house = pub_house;
        this.course_name = course_name;
        this.version = version;
        this.book_price = book_price;
    }

    @Override
    public String toString() {
        return book_id + " " + book_name + " " + author + " " + pub_house + " " + course_name + " " + version + " " + book_price+" "+ordered;
    }


}
