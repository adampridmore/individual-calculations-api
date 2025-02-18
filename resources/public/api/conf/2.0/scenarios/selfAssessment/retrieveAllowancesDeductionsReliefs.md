<p xmlns="http://www.w3.org/1999/html">Scenario simulations using Gov-Test-Scenario headers is only available in the sandbox environment.</p>
<table>
    <thead>
        <tr>
            <th>Header Value (Gov-Test-Scenario)</th>
            <th>Scenario</th>
        </tr>
    </thead>
    <tbody> 
        <tr>
            <td><p>N/A - DEFAULT</p></td>
            <td><p>Simulates returning a tax calculation with all fields.<br><br>Please note the values do not represent a realistic tax calculation and is intended to show all fields being populated.</p></td>
        </tr>
        <tr>
            <td><p>ERROR_MESSAGES_EXIST</p></td>
            <td><p>Simulates the scenario where errors exist and no calculation has been generated.</p></td>
        </tr>
        <tr>
            <td><p>NO_ALLOWANCES_DEDUCTIONS_RELIEFS_EXIST</p></td>
            <td>
                <p>Simulates the scenario where no allowances, deductions and reliefs data exists.</p>
                <p><strong>
                    Note: this scenario no longer triggers an error.
                    Instead, a 200 response with zero totalAllowancesAndDeductions, zero totalReliefs, and empty details will be returned.
                </strong></p>
            </td>
        </tr>
        <tr>
            <td><p>NOT_FOUND</p></td>
            <td><p>Simulates the scenario where no data can be found.</p></td>
        </tr>
        <tr>
            <td><p>UK_MULTIPLE_INCOMES_EXAMPLE</p></td>
            <td><p>Simulates an example tax calculation with realistic data values, for a UK FHL Property business with Savings and Dividends.</p></td>
        </tr>
        <tr>
            <td><p>UK_PROP_DIVIDENDS_EXAMPLE</p></td>
            <td><p>Simulates an example tax calculation with realistic data values, for a UK Non-FHL Property business with Dividends.</p></td>
        </tr>   
        <tr>
            <td><p>UK_PROP_GIFTAID_EXAMPLE</p></td>
            <td><p>Simulates an example tax calculation with realistic data values, for a UK Non-FHL Property business with Gift Aid.</p></td>
        </tr>        
        <tr>
            <td><p>UK_PROP_SAVINGS_EXAMPLE</p></td>
            <td><p>Simulates an example tax calculation with realistic data values, for a UK Non-FHL Property business with Savings.</p></td>
        </tr>
        <tr>
            <td><p>UK_SE_SAVINGS_EXAMPLE</p></td>
            <td><p>Simulates an example tax calculation with realistic data values, for a UK Self-Employment business with Savings.</p></td>
        </tr>
        <tr>
            <td><p>SCOT_SE_DIVIDENDS_EXAMPLE</p></td>
            <td><p>Simulates an example tax calculation with realistic data values, for a Scottish Self-Employment business with Dividends.</p></td>
        </tr>                    
    </tbody>
</table>