package vn.edu.hcmuaf.fit.model;

public class InformationDeliverModel {
    int id, idCart, x,y,z,w;
    String districtTo, warTo;

    public InformationDeliverModel(int id, int idCart, int x, int y, int z, int w, String districtTo, String warTo) {
        this.id = id;
        this.idCart = idCart;
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.districtTo = districtTo;
        this.warTo = warTo;
    }

    public InformationDeliverModel() {
    }

    public InformationDeliverModel(int idCart, int x, int y, int z, int w, String districtTo, String warTo) {
        this.idCart = idCart;
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.districtTo = districtTo;
        this.warTo = warTo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCart() {
        return idCart;
    }

    public void setIdCart(int idCart) {
        this.idCart = idCart;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public String getDistrictTo() {
        return districtTo;
    }

    public void setDistrictTo(String districtTo) {
        this.districtTo = districtTo;
    }

    public String getWarTo() {
        return warTo;
    }

    public void setWarTo(String warTo) {
        this.warTo = warTo;
    }

    @Override
    public String toString() {
        return "InformationDeliverModel{" +
                "id=" + id +
                ", idCart=" + idCart +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", w=" + w +
                ", districtTo='" + districtTo + '\'' +
                ", warTo='" + warTo + '\'' +
                '}';
    }
}
