package example;

public enum AuthLevel {
    ADMIN(2),
    USER(1);

    public final int value;

    private AuthLevel(int value){
        this.value = value;
    }
    // Static method to get enum from a string
    public static AuthLevel fromString(String name) throws IllegalArgumentException{
        try {
            name = name.replace("ROLE_","");
            return AuthLevel.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No enum constant for input: " + name);
        }
    }
}
