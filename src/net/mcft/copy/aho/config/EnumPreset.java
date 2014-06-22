package net.mcft.copy.aho.config;

public enum EnumPreset {
	
	//              [   REGENERATION   ] [     HURT PENALTY      ]
	PEACEFUL      (   1.0,  0, 10, 2.5,    0.5,  2.0,   4.0,  6.0  ),
	EASY          (   3.0, 10, 15, 2.0,    1.0,  3.0,  10.0, 15.0  ),
	NORMAL        (   8.0, 13, 17, 3.0,    2.0,  8.0,  30.0, 20.0  ),
	HARD          (  20.0, 14, 18, 3.5,   12.0, 30.0,  90.0, 30.0  ),
	HARDCORE      (  40.0, 14, 18, 4.0,   20.0, 80.0, 180.0, 40.0  ),
	ULTRAHARDCORE (   0.0,  0,  0, 0.0,    0.0,  0.0,   0.0,  0.0  ),
	CUSTOM        (   0.0,  0,  0, 0.0,    0.0,  0.0,   0.0,  0.0  );
	
	public final double regenHealTime;
	public final int regenHungerMinimum;
	public final int regenHungerMaximum;
	public final double regenExhaustion;
	
	public final double hurtTime;
	public final double hurtTimeMaximum;
	public final double hurtMaximum;
	public final double hurtBuffer;
	
	EnumPreset(double regenHealTime, int regenHungerMinimum, int regenHungerMaximum, double regenExhaustion,
	           double hurtTime, double hurtTimeMaximum, double hurtMaximum, double hurtBuffer) {
		this.regenHealTime = regenHealTime;
		this.regenHungerMinimum = regenHungerMinimum;
		this.regenHungerMaximum = regenHungerMaximum;
		this.regenExhaustion = regenExhaustion;
		this.hurtTime = hurtTime;
		this.hurtTimeMaximum = hurtTimeMaximum;
		this.hurtMaximum = hurtMaximum;
		this.hurtBuffer = hurtBuffer;
	}
	
}
