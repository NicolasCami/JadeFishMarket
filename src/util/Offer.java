package util;

import java.io.Serializable;

public class Offer implements Serializable{
    
    private int         _price;
    private int         _delay;
    private int         _step;
    private String      _seller;
    private String      _buyer;
    private String      _name;
    private boolean     _closed;

    public Offer(String name, int price, int delay, int step, String seller) {
        _name = name;
        _price = price;
        _delay = delay;
        _step = step;
        _seller = seller;
        _closed = false;
    }

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }

    public int getPrice() {
        return _price;
    }

    public void setPrice(int price) {
        _price = price;
        if(_price < 1) {
            _price = 1;
        }
    }

    public String getSeller() {
        return _seller;
    }

    public void setSeller(String _seller) {
        this._seller = _seller;
    }

    public int getDelay() {
        return _delay;
    }

    public void setDelay(int _delay) {
        this._delay = _delay;
    }

    public int getStep() {
        return _step;
    }

    public void setStep(int _step) {
        this._step = _step;
    }

    public String getBuyer() {
        return _buyer;
    }

    public void setBuyer(String _buyer) {
        this._buyer = _buyer;
    }

    public boolean isClosed() {
        return _closed;
    }

    public void setClosed(boolean _closed) {
        this._closed = _closed;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj==this) {
            return true;
        }
 
        if (obj instanceof Offer) {
            Offer other = (Offer) obj;

            if (_seller == null ? other._seller != null : !_seller.equals(other._seller)) {
                if (_seller == null || !_seller.equals(other._seller)) {
                    return false;
                }
            }
            
            if (_name == null ? other._name != null : !_name.equals(other._name)) {
                if (_name == null || !_name.equals(other._name)) {
                    return false;
                }
            }
 
            return true;
        }
 
        return false;
    }
    
    @Override
    public String toString() {
        return "[" + _name + ", vendu par " + _seller + ", proposé à " + _price + ", acheteur " + _buyer + "]";
    }
    
}
