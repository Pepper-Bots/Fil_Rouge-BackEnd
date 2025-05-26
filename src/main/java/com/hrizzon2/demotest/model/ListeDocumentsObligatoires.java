package com.hrizzon2.demotest.model;

import com.hrizzon2.demotest.model.enums.TypeDocument;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ListeDocumentsObligatoires {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Formation formation;

    @Enumerated(EnumType.STRING)
    private TypeDocument typeDocument;
}
