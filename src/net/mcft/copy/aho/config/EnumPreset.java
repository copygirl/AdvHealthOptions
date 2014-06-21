package net.mcft.copy.aho.config;

public enum EnumPreset {
	
	//              [   REGENERATION   ] [     HURT PENALTY      ]
	PEACEFUL      (   1.0,  0, 10, 2.5,    0.5,  2.0,   3.0,  6.0  ),
	EASY          (   1.5, 10, 15, 2.0,    1.0,  3.0,   8.0, 12.0  ),
	NORMAL        (   3.5, 13, 17, 3.0,    2.0,  8.0,  20.0, 15.0  ),
	HARD          (  10.0, 15, 18, 3.5,    5.0, 25.0,  40.0, 20.0  ),
	HARDCORE      (  30.0, 15, 18, 4.0,   10.0, 60.0, 120.0, 40.0  ),
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
