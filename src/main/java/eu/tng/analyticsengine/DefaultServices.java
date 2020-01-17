/*
## Copyright (c) 2015 SONATA-NFV, 2017 5GTANGO [, ANY ADDITIONAL AFFILIATION]
## ALL RIGHTS RESERVED.
##
## Licensed under the Apache License, Version 2.0 (the "License");
## you may not use this file except in compliance with the License.
## You may obtain a copy of the License at
##
##     http://www.apache.org/licenses/LICENSE-2.0
##
## Unless required by applicable law or agreed to in writing, software
## distributed under the License is distributed on an "AS IS" BASIS,
## WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
## See the License for the specific language governing permissions and
## limitations under the License.
##
## Neither the name of the SONATA-NFV, 5GTANGO [, ANY ADDITIONAL AFFILIATION]
## nor the names of its contributors may be used to endorse or promote
## products derived from this software without specific prior written
## permission.
##
## This work has been performed in the framework of the SONATA project,
## funded by the European Commission under Grant number 671517 through
## the Horizon 2020 and 5G-PPP programmes. The authors would like to
## acknowledge the contributions of their colleagues of the SONATA
## partner consortium (www.sonata-nfv.eu).
##
## This work has been performed in the framework of the 5GTANGO project,
## funded by the European Commission under Grant number 761493 through
## the Horizon 2020 and 5G-PPP programmes. The authors would like to
## acknowledge the contributions of their colleagues of the 5GTANGO
## partner consortium (www.5gtango.eu).
 */
package eu.tng.analyticsengine;

import eu.tng.repository.dao.AnalyticServiceRepository;
import eu.tng.repository.domain.AnalyticService;
import java.util.LinkedList;
import java.util.List;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author eleni
 */
public class DefaultServices {

    @Autowired
    private AnalyticServiceRepository analyticServiceRepository;

    public String getAnalyticServiceList() {
        analyticServiceRepository.deleteAll();

        // save a couple of customers
        AnalyticService as1 = new AnalyticService();
        as1.setName("correlogram");
        as1.setUrl("/ocpu/library/Physiognomica/R/correlogram");
        as1.setDescription("Provide a correlogram with high statistical correlations between metrics");
        as1.setConstraints("Select the set of metrics (more than one) to be used for the calculation of the correlation matrix");

        List<String> results1 = new LinkedList<>();
        results1.add("correlogram.html");
        results1.add("correlogram.svg");
        as1.setResults(results1);
        analyticServiceRepository.save(as1);

        AnalyticService as5 = new AnalyticService();
        as5.setName("chord");
        as5.setUrl("/ocpu/library/Physiognomica/R/chord");
        as5.setDescription("Provide a correlogram with high statistical correlations between metrics via an interactive chord diagram");
        as5.setConstraints("Select the set of metrics (more than one) to be used for the calculation of the correlation matrix");
        List<String> results5 = new LinkedList<>();
        results5.add("correlation_page.html");
        as5.setResults(results5);
        analyticServiceRepository.save(as5);

        AnalyticService as2 = new AnalyticService();
        as2.setName("TimeSeriesDecomposition");
        as2.setUrl("/ocpu/library/Physiognomica/R/time_series_decomposition");
        as2.setDescription("Provide a decomposition of a time series in seasonal, trend, and remainder parts");
        as2.setConstraints("Select one metric to be used for time series decomposition");
        List<String> results2 = new LinkedList<>();
        results2.add("time_series_decomposition.html");
        as2.setResults(results2);
        analyticServiceRepository.save(as2);

        AnalyticService as3 = new AnalyticService();
        as3.setName("linear_regression");
        as3.setUrl("/ocpu/library/Physiognomica/R/linear_regression");
        as3.setDescription("Provide a linear regression model along with a scatterplot");
        as3.setConstraints("Select the dependent and intenpendent variable for the linear regression model");
        List<String> results3 = new LinkedList<>();
        results3.add("linear_regression.html");
        as3.setResults(results3);
        analyticServiceRepository.save(as3);

        AnalyticService as4 = new AnalyticService();
        as4.setName("multiple_linear_regression");
        as4.setUrl("/ocpu/library/Physiognomica/R/multiple_linear_regression");
        as4.setDescription("Provide a multiple linear regression model along with composed visualizations");
        as4.setConstraints("Select the dependent and intenpendent variable for the linear regression model");
        List<String> results4 = new LinkedList<>();
        results4.add("multiple_linear_regression.html");
        as4.setResults(results4);
        analyticServiceRepository.save(as4);

        AnalyticService as6 = new AnalyticService();
        as6.setName("test");
        as6.setUrl("/ocpu/library/Physiognomica/R/test");
        as6.setDescription("test");
        as6.setConstraints("test");
        List<String> results6 = new LinkedList<>();
        //results6.add("console");
        results6.add("test.html");
        as6.setResults(results6);
        analyticServiceRepository.save(as6);

        AnalyticService as7 = new AnalyticService();
        as7.setName("filter_healthy_metrics");
        as7.setUrl("/ocpu/library/Physiognomica/R/filter_healthy_metrics");
        as7.setDescription("Filter healthy metrics");
        as7.setConstraints("No constraint");
        List<String> results7 = new LinkedList<>();
        results7.add("json");
        results7.add("filter_healthy_metrics.html");
        as7.setResults(results7);
        analyticServiceRepository.save(as7);

        AnalyticService as8 = new AnalyticService();
        as8.setName("matplot_linear_regression");
        as8.setUrl("/panalytics/api/v1.0/prometheusMetricsAnalyzer/linear_regression/ln");
        as8.setDescription("matplot_linear_regression");
        as8.setConstraints("Select the dependent and intenpendent variable for the linear regression model");
        List<String> results8 = new LinkedList<>();
        results8.add("linear_regression.html");
        as8.setResults(results8);
        analyticServiceRepository.save(as8);

        List<AnalyticService> analyticServicesList = analyticServiceRepository.findAll();
        JSONArray asl = new JSONArray(analyticServicesList);

        return asl.toString();
    }

}
