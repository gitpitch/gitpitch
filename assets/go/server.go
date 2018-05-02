package funding

type FundServer struct {
    Commands chan interface{}
    fund Fund
}

func NewFundServer(initialBalance int) *FundServer {
    server := &FundServer{
        // make() creates builtins like channels
        Commands: make(chan interface{}),
        fund: NewFund(initialBalance),
    }

    // Spawn off the server's main loop immediately
    go server.loop()
    return server
}

func (s *FundServer) loop() {
    // The built-in "range" clause can iterate
    // over channels, amongst other things
    for command := range s.Commands {
    
        // Handle the command
        
    }
}

