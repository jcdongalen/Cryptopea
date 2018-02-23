package com.github.jc.cryptopea.Utils;

import com.github.jc.cryptopea.Interfaces.onHotelFoundListener;

/**
 * Created by Owner on 2/15/2018.
 */

public class HotelFinder {

    private onHotelFoundListener onHotelFoundListener;

    public HotelFinder(onHotelFoundListener onHotelFoundListener) {
        this.onHotelFoundListener = onHotelFoundListener;
    }

    public void startFindingHotel(String userName, String passWord) {
        if (userName.equalsIgnoreCase("") || passWord.equalsIgnoreCase("")) {
            onHotelFoundListener.onHotelNotFound();
        } else {
            onHotelFoundListener.onHotelFound();
        }
    }

}
