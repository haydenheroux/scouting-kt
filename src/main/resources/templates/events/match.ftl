<#-- @ftlvariable name="match" type="io.github.haydenheroux.scouting.models.match.Match" -->
<#import "/common/_layout.ftl" as layout />
<#assign title="TODO">
<#assign heading="TODO">
<@layout.header title=title>
    <h1>${heading}</h1>
    <hr/>
    <#list match.participants as participant>
        <section>
            <!-- <#assign seasonReference=participantReference.robotReference.seasonReference> -->
            <!-- <#assign teamReference=seasonReference.teamReference> -->
            <!-- <@layout.season_link season=seasonReference size="small" /> -->
            <!-- <@layout.alliance alliance=participantReference.alliance /> -->
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
