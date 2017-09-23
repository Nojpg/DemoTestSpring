package packageTest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ItemProcessor;

/**
 * Created by Nojpg on 15.09.17.
 */

public class PersonItemProcessor implements ItemProcessor<Person, Person> {
    private static Log log = LogFactory.getLog(PersonItemProcessor.class);

    @Override
    public Person process(Person person) throws Exception {
        String firstName = person.getFirstName().toUpperCase();
        String lastName = person.getLastName().toUpperCase();
        Person transformedPerson = new Person();
        transformedPerson.setFirstName(firstName);
        transformedPerson.setLastName(lastName);
        log.info("Transformed person: " + person + " Into: " + transformedPerson);
        return transformedPerson;
    }
}
