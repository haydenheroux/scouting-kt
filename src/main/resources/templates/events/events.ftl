<#-- @ftlvariable name="events" type="kotlin.collections.List<io.github.haydenheroux.scouting.models.event.Event>" -->
<#import "/common/_layout.ftl" as layout />
<@layout.header title="Events">
    <table>
        <thead>
            <tr>
                <th>Name</th>
                <th>Region</th>
                <th>Year</th>
                <th>Week</th>
            </tr>
        </thead>
        <tbody>
        <#list events?reverse as event>
            <tr>
                <td><@layout.event_link event=event /></td>
                <td>${layout.region_to_text(event.region)}</td>
                <td>${event.year?c}</td>
                <td>${event.week?c}</td>
            </tr>
        </#list>
        </tbody>
    </table>
</@layout.header>
