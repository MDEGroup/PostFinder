package net.camelpe.examples.loanbroker.queue;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class BankResponseAggregationStrategy implements AggregationStrategy {

	private final transient Logger log = LoggerFactory.getLogger(getClass());

	// Here we put the bank response together
	@Override
	public Exchange aggregate(final Exchange oldExchange,
			final Exchange newExchange) {
		this.log.debug("Get the exchange to aggregate, older: " + oldExchange
				+ " newer:" + newExchange);

		// the first time we only have the new exchange
		if (oldExchange == null) {
			return newExchange;
		}

		final Message oldMessage = oldExchange.getIn();
		final Message newMessage = newExchange.getIn();

}}