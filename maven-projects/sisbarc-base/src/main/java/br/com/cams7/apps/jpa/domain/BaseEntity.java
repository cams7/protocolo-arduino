/**
 * 
 */
package br.com.cams7.apps.jpa.domain;

import java.io.Serializable;

/**
 * @author cams7
 *
 * @param <PK>
 */
public abstract class BaseEntity<PK extends Serializable> implements
		Serializable {
	private static final long serialVersionUID = 1L;

	public static final byte ALLOCATION_SIZE = 1;
	public static final byte INITIAL_VALUE = 1;

	public BaseEntity() {
		super();
	}

	/**
	 * @param id
	 */
	public BaseEntity(PK id) {
		this();

		setId(id);
	}

	public abstract PK getId();

	public abstract void setId(PK id);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.domain.Persistable#isNew()
	 */
	public boolean isNew() {
		return null == getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("Entity of type %s with id: %s", this.getClass()
				.getName(), getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object entity) {

		if (null == entity) {
			return false;
		}

		if (this == entity) {
			return true;
		}

		if (!getClass().equals(entity.getClass())) {
			return false;
		}

		BaseEntity<?> e = (BaseEntity<?>) entity;

		return null == this.getId() ? false : this.getId().equals(e.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		int hashCode = 17;

		hashCode += null == getId() ? 0 : getId().hashCode() * 31;

		return hashCode;
	}

}
