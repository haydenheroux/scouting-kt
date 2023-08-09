<#-- @ftlvariable name="matchReference" type="io.github.haydenheroux.scouting.models.match.MatchReference" -->
<#import "/common/_layout.ftl" as layout />
<#assign event=matchReference.eventReference>
<#assign title="${layout.match_type_to_text(matchReference.type)} ${matchReference.number} - ${event.name}">
<@layout.header title=title>
    <h1>${layout.match_type_to_text(matchReference.type)} ${matchReference.number} - <a href="/events/${layout.region_to_serial(event.region)}/${event.year?c}/${event.week?c}/${event.name}">${event.name}</a></h1>
    <hr/>
    <#list matchReference.participantReferences as participantReference>
        <section>
            <#assign seasonReference=participantReference.robotReference.seasonReference>
            <#assign teamReference=seasonReference.teamReference>
            <@layout.season_link season=seasonReference size="small" />
            <@layout.alliance alliance=participantReference.alliance />
            <h3>Metrics</h3>
            <table>
                <thead>
                    <tr>
                        <th>Key</th>
                        <th>Value</th>
                    </tr>
                </thead>
                <tbody>
                <#list participantReference.metricReferences as metricReference>
                    <tr>
                        <td>${metricReference.key}</td>
                        <td>${metricReference.value}</td>
                    </tr>
                </#list>
                </tbody>
            </table>
        </section>
        <hr/>
    </#list>
</@layout.header>
