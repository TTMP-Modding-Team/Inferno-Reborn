package ttmp.infernoreborn.compat.jei.sigil;

public enum RecipeSize{
	SIZE_3X3, SIZE_5X5, SIZE_7X7;
	int getIntSize(){
		switch(this){
			case SIZE_3X3:
				return 3;
			case SIZE_5X5:
				return 5;
			case SIZE_7X7:
				return 7;
			default:
				return 3;
		}
	}
}
