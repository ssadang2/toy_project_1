package toy.ktx.domain.dto;

import lombok.Data;
import toy.ktx.domain.Passenger;

@Data
public class PassengerDto {

    private Integer toddler;

    private Integer kids;

    private Integer adult;

    private Integer senior;

    public Passenger dtotoPassenger() {
        Passenger passenger = new Passenger();

        passenger.setToddler(this.getToddler());

        passenger.setKids(this.getKids());

        passenger.setAdult(this.getAdult());

        passenger.setSenior(this.getSenior());

        return passenger;
    }

    public Integer howManyOccupied() {

        if (toddler == null) {
            toddler = 0;
        }

        if (kids == null) {
            kids = 0;
        }

        if (adult == null) {
            adult = 0;
        }

        if (senior == null) {
            senior = 0;
        }

        return toddler + kids + adult + senior;
    }

    //요금은 거리 상관없이 2만원이라고 일단 가정
    public Long getFee() {
        return Long.valueOf(toddler * 6000 + kids * 10000 + adult * 20000 + senior * 14000);
    }
}
