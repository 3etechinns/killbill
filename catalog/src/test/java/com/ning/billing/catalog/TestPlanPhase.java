/*
 * Copyright 2010-2011 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.ning.billing.catalog;

import com.ning.billing.catalog.api.BillingPeriod;
import com.ning.billing.catalog.api.CatalogApiException;
import com.ning.billing.catalog.api.PhaseType;
import com.ning.billing.util.config.ValidationErrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestPlanPhase {
	Logger log = LoggerFactory.getLogger(TestPlanPhase.class);
	
	@Test(enabled=true)
	public void testValidation() {
		log.info("Testing Plan Phase Validation");
		
		DefaultPlanPhase pp = new MockPlanPhase().setBillCycleDuration(BillingPeriod.MONTHLY).setReccuringPrice(null).setFixedPrice(new DefaultInternationalPrice());
		ValidationErrors errors = pp.validate(new MockCatalog(), new ValidationErrors());
		errors.log(log);
		Assert.assertEquals(errors.size(), 1);

		pp = new MockPlanPhase().setBillCycleDuration(BillingPeriod.NO_BILLING_PERIOD).setReccuringPrice(new MockInternationalPrice());
		errors = pp.validate(new MockCatalog(), new ValidationErrors());
		errors.log(log);
		Assert.assertEquals(errors.size(), 1);

		pp = new MockPlanPhase().setReccuringPrice(null).setFixedPrice(null).setBillCycleDuration(BillingPeriod.NO_BILLING_PERIOD);
		errors = pp.validate(new MockCatalog(), new ValidationErrors());
		errors.log(log);
		Assert.assertEquals(errors.size(), 1);
	}
	
	@Test
	public void testPhaseNames() throws CatalogApiException {
		String planName = "Foo";
		String planNameExt = planName + "-";
		
		DefaultPlan p = new MockPlan().setName(planName);
		DefaultPlanPhase ppDiscount = new MockPlanPhase().setPhaseType(PhaseType.DISCOUNT).setPlan(p);
		DefaultPlanPhase ppTrial = new MockPlanPhase().setPhaseType(PhaseType.TRIAL).setPlan(p);
		DefaultPlanPhase ppEvergreen = new MockPlanPhase().setPhaseType(PhaseType.EVERGREEN).setPlan(p);
		DefaultPlanPhase ppFixedterm = new MockPlanPhase().setPhaseType(PhaseType.FIXEDTERM).setPlan(p);
		
		String ppnDiscount = DefaultPlanPhase.phaseName(p, ppDiscount);
		String ppnTrial = DefaultPlanPhase.phaseName(p, ppTrial);
		String ppnEvergreen = DefaultPlanPhase.phaseName(p, ppEvergreen);
		String ppnFixedterm = DefaultPlanPhase.phaseName(p, ppFixedterm);
		
		Assert.assertEquals(ppnTrial, planNameExt + "trial");
		Assert.assertEquals(ppnEvergreen, planNameExt + "evergreen");
		Assert.assertEquals(ppnFixedterm, planNameExt + "fixedterm");
		Assert.assertEquals(ppnDiscount, planNameExt + "discount");
		
		
		Assert.assertEquals(DefaultPlanPhase.planName(ppnDiscount),planName);
		Assert.assertEquals(DefaultPlanPhase.planName(ppnTrial),planName);
		Assert.assertEquals(DefaultPlanPhase.planName(ppnEvergreen), planName);
		Assert.assertEquals(DefaultPlanPhase.planName(ppnFixedterm), planName);
		
		
		
		
	}
}
