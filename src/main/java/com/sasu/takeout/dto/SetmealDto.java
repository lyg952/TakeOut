package com.sasu.takeout.dto;


import com.sasu.takeout.entity.Setmeal;
import com.sasu.takeout.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
