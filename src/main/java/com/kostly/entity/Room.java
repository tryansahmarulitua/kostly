package com.kostly.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
@Entity
@Table(name = "rooms")
public class Room {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Column(name="room_number", nullable=false, unique=true, length=20)
    private String roomNumber;
    @NotBlank @Column(nullable=false, length=80)
    private String type;
    @NotNull @DecimalMin("0.0") @Column(nullable=false, precision=14, scale=2)
    private BigDecimal price;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20)
    private RoomStatus status = RoomStatus.AVAILABLE;
    @Column(length=500) private String description;
    public Room() {}
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getRoomNumber(){return roomNumber;} public void setRoomNumber(String v){this.roomNumber=v;}
    public String getType(){return type;} public void setType(String v){this.type=v;}
    public BigDecimal getPrice(){return price;} public void setPrice(BigDecimal v){this.price=v;}
    public RoomStatus getStatus(){return status;} public void setStatus(RoomStatus v){this.status=v;}
    public String getDescription(){return description;} public void setDescription(String v){this.description=v;}
}
