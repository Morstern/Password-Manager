-module(passwordmanager).
-compile([debug_info,export_all]).

-record(state, {data}).

start() ->
    register(?MODULE, spawn(?MODULE, init, [])).

init() ->
    loop(#state{data=readDataToRecord()}).

loop(S) ->
    receive
        {From, Operation, Data} ->
            case Operation of
                getData ->
                    From ! S#state.data,
                    loop(S);
                createPassword ->
                    {ServiceName, Login, Password} = Data,
                    NewSaltHash = getHashedSalt(getSalt(erlang:localtime())),
                    NewPasswordEncrypted = createPassword(Password, NewSaltHash),
                    NewId = getLatestId(S#state.data),
                    NewData = S#state.data ++ [{NewId, ServiceName, Login, NewPasswordEncrypted, binary_to_list(NewSaltHash)}],
                    saveRecords(NewData),
                    From ! {NewId, ServiceName, Login},
                    loop(S#state{data=NewData});
                getPassword ->
                    {Id, Password} = Data,
                    case searchRecord(S#state.data, Id) of
                        {found, Record} -> 
                            From ! checkPassword(Password, Record),
                            loop(S);
                        _ ->
                            From ! {not_found},
                            loop(S)
                    end,                  
                    loop(S);
                removePassword ->
                    {Id} = Data,
                    case searchRecord(S#state.data, Id) of
                        {found, Record} -> 
                            NewData = S#state.data--[Record],
                            case length(NewData) of
                                0 -> 
                                    clearFile();
                                _ -> 
                                    saveRecords(NewData)
                            end,
                            loop(S#state{data = NewData});
                        _ ->
                            loop(S)
                    end;
                _ ->
                    From ! "Unknown",
                    loop(S)
            end,
            loop(S);
        _ ->
            io:format("Unknown message: ~n"),
            loop(S)
    end.

% I/O to Files
readDataToRecord() ->
    {ok, F} = file:open("data.txt", [read]),
    readWhile(F,[]).

readWhile(F,Data) ->
    case io:get_line(F, '') of
        eof -> 
            file:close(F),
            Data;
        Line ->
            {Id, ServiceName, Login, PasswordEncrypted, SaltHash} = list_to_tuple(string:tokens(Line,":")), 
            readWhile(F,Data++[{Id, ServiceName, Login, PasswordEncrypted, base64:decode(SaltHash)}])
    end.

clearFile()->
    {ok, F} = file:open("data.txt", [write]),
    file:close(F).

saveRecords(Passwords) ->
    {ok, F} = file:open("data.txt", [write]),
    saveWhile(F,Passwords).

saveWhile(F, [{Id, ServiceName, Login, PasswordEncrypted, SaltHash}|T]) ->
    io:format(F, "~s:~s:~s:~s:~s~n",[Id, ServiceName, Login, PasswordEncrypted, base64:encode(SaltHash)]),
    case T of 
        [] -> 
            file:close(F),
            ok;
        _ ->
            saveWhile(F,T)
    end.

searchRecord([], _)->
    not_found;

searchRecord([Record={IdS, _, _, _, _}|T], Id) ->
    case IdS == Id of
        true -> 
            {found, Record};
        _ -> 
            searchRecord(T, Id)
    end.

% Server Logic Functions
getSalt({{Year, Month, Day}, {Hour, Minute, Sec}}) ->
    integer_to_list(Year)++integer_to_list(Month)++integer_to_list(Day)++integer_to_list(Hour)++integer_to_list(Minute)++integer_to_list(Sec).

getHashedSalt(Salt) ->
    crypto:hash_final(crypto:hash_update(crypto:hash_init(sha224), Salt)).

createPassword(Password, SaltHash) ->
    lists:sublist(binary_to_list(base64:encode(binary_to_list(crypto:hash_final(crypto:hash_update(crypto:hash_init(sha224), Password++SaltHash))))), 1, 16).

checkPassword(Password, {_,_,_,PasswordEncrypted,SaltHash}) ->
    case PasswordEncrypted == createPassword(Password,SaltHash) of
        true -> 
            {correct, PasswordEncrypted};
        false -> 
            {uncorrect}
    end.

getLatestId(Passwords) ->
    case length(Passwords) of
        0 -> 
            "0";
        _ ->
            [{Id,_,_,_,_}] = lists:nthtail(length(Passwords)-1, Passwords),
            integer_to_list(list_to_integer(Id)+1)
    end.