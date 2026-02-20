package amp.awec.util;

import java.util.Objects;

public class Vec3i {
	public int x, y, z;

	public static final Vec3i UP = new Vec3i(0, 1, 0);
	public static final Vec3i DOWN = new Vec3i(0, -1, 0);
	public static final Vec3i NORTH = new Vec3i(0, 0, -1);
	public static final Vec3i SOUTH = new Vec3i(0, 0, 1);
	public static final Vec3i EAST = new Vec3i(1, 0, 0);
	public static final Vec3i WEST = new Vec3i(-1, 0, 0);

	public Vec3i() {
		set(0, 0,0 );
	}

	public Vec3i(int x, int y, int z) {
		set(x, y, z);
	}

	public Vec3i(Vec3i other) {
		set(other.x, other.y, other.z);
	}

	public void set(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public String toString() {
		return this.x + ", " + this.y + ", " + this.z;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Vec3i)) return false;
		Vec3i other = (Vec3i) o;
		return this.x == other.x && this.y == other.y && this.z == other.z;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, z);
	}

	public void addi(Vec3i other) {
		this.x += other.x;
		this.y += other.y;
		this.z += other.z;
	}

	public Vec3i add(Vec3i other) {
		Vec3i v = new Vec3i(this);
		v.addi(other);
		return v;
	}

	public Vec3i subtract(Vec3i other) {
		return new Vec3i(
			this.x - other.x,
			this.y - other.y,
			this.z - other.z
		);
	}

	public Vec3i componentMultiply(Vec3i other) {
		return new Vec3i(
			this.x * other.x,
			this.y * other.y,
			this.z * other.z
		);
	}

	public void scalei(int scalar) {
		this.x *= scalar;
		this.y *= scalar;
		this.z *= scalar;
	}

	public Vec3i scale(int scalar) {
		Vec3i v = new Vec3i(this);
		v.scalei(scalar);
		return v;
	}
}
