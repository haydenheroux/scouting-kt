<#-- @ftlvariable name="event" type="io.github.haydenheroux.scouting.models.event.Event" -->
<#-- @ftlvariable name="match" type="io.github.haydenheroux.scouting.models.match.Match" -->
<#import "/common/_layout.ftl" as layout />
<#assign match_text="${layout.match_type_to_text(match.type)} ${match.number}">
<@layout.header title="${match_text} - ${event.name}">
    <h1>${match_text} - <@layout.event_link event=event /></h1>
    <hr/>
    <#list match.participants as participant>
        <section>
            <h2><@layout.team_year_link team=participant.team year=event.year /><h2>
            <@layout.alliance alliance=participant.alliance />
            <h3>Metrics</h3>
            <table>
                <thead>
                    <tr>
                        <th>Key</th>
                        <th>Value</th>
                    </tr>
                </thead>
                <tbody>
                <#list participant.metrics as metric>
                    <tr>
                        <td>${metric.key}</td>
                        <td>${metric.value}</td>
                    </tr>
                </#list>
                </tbody>
            </table>
        </section>
        <hr/>
    </#list>
</@layout.header>
