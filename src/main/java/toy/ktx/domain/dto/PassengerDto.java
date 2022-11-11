package toy.ktx.domain.dto;

import lombok.Data;

@Data
public class PassengerDto {

    private Integer toddler;

    private Integer kids;

    private Integer adult;

    private Integer senior;

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
}
