package com.neotys.errors.export;

/**
 * The 3 steps that a virtual user can have
 *
 */
public enum VirtualUserStep {
	ALL(-1, "All"),
	INIT(0, "Init"),
	ACTIONS(1, "Actions"),
	END(2, "End");

	private final int id;
	private final String name;

	private VirtualUserStep(final int id, final String name) {
		this.id = id;
		this.name = name;
	}

	public int getValue() {
		return this.id;
	}

	public static VirtualUserStep make(final int intStep) {
		for (final VirtualUserStep oneStep : values()) {
			if (oneStep.getValue() == intStep) {
				return oneStep;
			}
		}
		throw new IllegalArgumentException("Invalid VirtualUserStep index " + intStep);
	}

	@Override
	public String toString() {
		return name;
	}

	public static VirtualUserStep make(final String virtualUserStepName) {
		for (final VirtualUserStep oneStep : values()) {
			if (oneStep.name.equals(virtualUserStepName)) {
				return oneStep;
			}
		}
		throw new IllegalArgumentException("Invalid VirtualUserStep name " + virtualUserStepName);
	}
}