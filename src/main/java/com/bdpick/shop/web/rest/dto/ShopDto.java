package com.bdpick.shop.web.rest.dto;

import com.bdpick.common.domain.AuditDate;
import com.bdpick.shop.domain.Shop;
import com.bdpick.shop.domain.ShopImage;
import com.bdpick.user.domain.User;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(uniqueConstraints =
        {
                @UniqueConstraint(name = "UNQ_SHOP_USER_ID", columnNames = {"user_id"}),
                @UniqueConstraint(name = "UNQ_SHOP_REGISTER_NUMBER", columnNames = {"registerNumber"})
        }
)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@EqualsAndHashCode(callSuper = true)
public class ShopDto extends Shop implements Serializable {


}
