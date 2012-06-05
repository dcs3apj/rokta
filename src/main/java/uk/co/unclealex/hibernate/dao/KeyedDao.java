package uk.co.unclealex.hibernate.dao;

import uk.co.unclealex.hibernate.model.KeyedBean;

public interface KeyedDao<T extends KeyedBean<T>> extends KeyedReadOnlyDao<T> {

	public void store(T keyedBean);
	public void remove(T keyedBean);

}
