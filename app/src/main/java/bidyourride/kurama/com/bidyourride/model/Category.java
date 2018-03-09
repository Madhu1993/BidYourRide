package bidyourride.kurama.com.bidyourride.model;

/**
 * Created by madhukurapati on 3/7/18.
 */

public class Category {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public Category(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString(){
        return name;
    }
}
