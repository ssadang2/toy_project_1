package toy.ktx.domain.enums;

public enum Temp {

    HELLO("이거?"), WORLD("맞아?");

    private final String desc;

    Temp(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
