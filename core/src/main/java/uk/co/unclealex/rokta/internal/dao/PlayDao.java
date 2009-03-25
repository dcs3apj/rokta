/**
 * 
 */
package uk.co.unclealex.rokta.internal.dao;

import uk.co.unclealex.rokta.internal.model.Hand;
import uk.co.unclealex.rokta.internal.model.Person;

/**
 * @author alex
 *
 */
public interface PlayDao {

	public int countByPersonAndHand(Person person, Hand hand);
}
