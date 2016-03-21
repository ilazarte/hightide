-- @security.watchlistid

select *
from   security
where  symbol in (select security_symbol
                   from   watchlist_security
                   where  watchlist_id = ?)