package com.bogglespringboot.Model.Tables;

import jakarta.persistence.*;

@Entity
@Table(name="dictionary")
public class Dictionary {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;





}
